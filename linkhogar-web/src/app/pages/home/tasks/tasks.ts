import {ChangeDetectorRef, Component, effect, inject, OnInit, signal} from '@angular/core';
import {HomeTaskService} from '../../../services/homeTask/home-task-service';
import {AuthService} from '../../../services/auth/auth.service';
import {HomeTaskResponse, StatusReverseTranslator} from '../../../Models/homeTasks/HomeTaskResponse';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {PostIt} from './post-it/post-it';
import {
  CdkDrag,
  CdkDragDrop,
  CdkDragHandle,
  CdkDropList, CdkDropListGroup,
  moveItemInArray,
  transferArrayItem
} from '@angular/cdk/drag-drop';
import Swal from 'sweetalert2';
import {HomeService} from '../../../services/home/home-service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-tasks',
  imports: [CommonModule, ReactiveFormsModule, PostIt, CdkDropList, CdkDrag, CdkDropListGroup],
  templateUrl: './tasks.html',
  styleUrl: './tasks.scss',
})
export class Tasks {
  private homeTaskService = inject(HomeTaskService);
  private authService = inject(AuthService);
  private formBuilder = inject(FormBuilder);
  private changeDetectorRef = inject(ChangeDetectorRef);
  private homeService = inject(HomeService);
  private route = inject(ActivatedRoute);

  todoTasks: HomeTaskResponse[] = [];
  inProgressTasks: HomeTaskResponse[] = [];
  doneTasks: HomeTaskResponse[] = [];

  currentUser = this.authService.currentUser;
  homeMembers = this.homeService.members;

  // Formulario creador de tareas
  taskForm: FormGroup = this.formBuilder.group({
    title: ['', Validators.required],
    description: [''],
    startDate: [''],
    dueDate: [''],
    assignedUserId: [null]
  });

  constructor() {
    this.route.queryParams.subscribe(params => {
      if (params['modal'] === 'create') {
        // Le damos un pequeñísimo margen de tiempo (100ms) para que Angular renderice el HTML primero
        setTimeout(() => {
          document.getElementById('createButton')?.click();
        }, 100);
      }
    });

    effect(() => {
      // Escuchamos directamente la Signal cargada por HomeLayout
      const allTasks = this.homeTaskService.tasks();
      if (allTasks) {
        this.distributeTasks(allTasks);
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  distributeTasks(allTasks: HomeTaskResponse[]) {
    this.todoTasks = allTasks.filter(t => t.status === 'Por hacer');
    this.inProgressTasks = allTasks.filter(t => t.status === 'En curso');

    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

    this.doneTasks = allTasks.filter(t => {
      if (t.status !== 'Finalizada' || !t.completedAt) return false;
      return new Date(t.completedAt) >= sevenDaysAgo;
    });
  }

  onSubmitTask() {
    if (this.taskForm.invalid) return;

    const formValue = this.taskForm.value;

    const selectedMember = this.homeMembers().find(m => m.id === formValue.assignedUserId);
    const assignedName = selectedMember ? `${selectedMember.name}` : null;

    const newTaskRequest = {
      title: formValue.title,
      description: formValue.description,
      homeId: this.currentUser()?.homeId,
      assignedUserId: formValue.assignedUserId,
      assignedUserName: assignedName,
      createdByName: this.currentUser()?.firstName + " " + this.currentUser()?.lastName, // Sacamos el nombre del creador del token/signal
      startDate: formValue.startDate ? new Date(formValue.startDate): null,
      dueDate: formValue.dueDate ? new Date(formValue.dueDate) : null
    };

    this.homeTaskService.createTask(newTaskRequest).subscribe({
      next: () => {
        // Refrescamos la Signal central para que el effect reaccione automáticamente
        if (this.currentUser()?.homeId) {
          this.homeTaskService.getTasksByHome(this.currentUser()?.homeId!).subscribe();
        }
        this.taskForm.reset();
        document.getElementById('closeModalBtn')?.click();
      },
      error: (err) => console.error('Error al crear tarea', err)
    });
  }

  drop(event: CdkDragDrop<HomeTaskResponse[]>, newStatus: string) {
    // Si lo suelta en la misma columna, solo cambiamos el orden visual
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      return;
    }

    // 1. Guardamos el estado anterior por si falla la petición
    const movedTask = event.previousContainer.data[event.previousIndex];
    const previousStatus = movedTask.status;
    const previousCompletedAt = movedTask.completedAt;

    // 2. ACTUALIZACIÓN OPTIMISTA: Movemos visualmente la tarjeta a la nueva columna
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex,
    );

    // Actualizamos los datos visuales
    movedTask.status = newStatus as any; // Casteo según tu tipo
    if (newStatus === 'Finalizada') {
      movedTask.completedAt = new Date().toISOString();
    } else {
      movedTask.completedAt = undefined;
    }

    // 3. Traducimos el estado para el Backend
    const backendStatus = StatusReverseTranslator[newStatus as keyof typeof StatusReverseTranslator];

    // 4. Llamamos al Backend
    this.homeTaskService.updateTaskStatus(movedTask.id, backendStatus).subscribe({
      next: () => {
      },
      error: (err) => {
        console.error('Error al actualizar en el servidor. Revirtiendo cambios...', err);

        // 5. ROLLBACK: Revertimos el movimiento visual
        transferArrayItem(
          event.container.data,
          event.previousContainer.data,
          event.currentIndex,
          event.previousIndex,
        );

        // Restauramos los datos del objeto a como estaban antes
        movedTask.status = previousStatus;
        movedTask.completedAt = previousCompletedAt;

        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo actualizar el estado de la tarea.',
          confirmButtonText: "Aceptar",
          confirmButtonColor: "var(--color-acento)"
        });
      }
    });
  }

  onTaskDeleted(taskId: string) {
    // Filtramos los arrays para quitar la tarea que coincida con ese ID
    this.todoTasks = this.todoTasks.filter(t => t.id !== taskId);
    this.inProgressTasks = this.inProgressTasks.filter(t => t.id !== taskId);
    this.doneTasks = this.doneTasks.filter(t => t.id !== taskId);

    // La eliminamos también del signal global
    this.homeTaskService.tasks.update(tasks => tasks.filter(t => t.id !== taskId));

    // Le damos un toque a Angular para que actualice la vista al instante
    this.changeDetectorRef.detectChanges();
  }

  private updateTaskInSignal(updatedTask: HomeTaskResponse) {
    this.homeTaskService.tasks.update(currentTasks => {
      const index = currentTasks.findIndex(t => t.id === updatedTask.id);
      if (index !== -1) {
        const newTasks = [...currentTasks];
        newTasks[index] = { ...updatedTask };
        return newTasks;
      }
      return currentTasks;
    });
  }
}
