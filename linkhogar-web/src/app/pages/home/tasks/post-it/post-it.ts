import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {HomeTaskResponse} from '../../../../Models/homeTasks/HomeTaskResponse';
import {NgClass} from '@angular/common';
import {CdkDragHandle} from '@angular/cdk/drag-drop';
import {HomeTaskService} from '../../../../services/homeTask/home-task-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-post-it',
  imports: [
    NgClass,
    CdkDragHandle
  ],
  templateUrl: './post-it.html',
  styleUrl: './post-it.scss',
})
export class PostIt {
  private homeTaskService = inject(HomeTaskService);

  @Input({ required: true }) task!: HomeTaskResponse;

  // Por defecto amarillo, pero se puede sobrescribir
  @Input() colorClass: string = 'color-amarillo';

  // Por defecto el pin base (azul), pero se puede pasar red-pin o green-pin
  @Input() pinClass: string = '';

  @Output() taskDeleted = new EventEmitter<string>();

  delete(){
    Swal.fire({
      title: '¿Eliminar tarea?',
      text: `Se borrará "${this.task.title}" del tablón.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: 'var(--color-acento)',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {

        this.taskDeleted.emit(this.task.id);

        this.homeTaskService.deleteTask(this.task.id).subscribe({
          next: () => {},
          error: (err) => {
            console.error('Error eliminando la tarea', err);

            //ROLLBACK: Si falla, volvemos a meter la tarea entera en la memoria global (Signal)
            this.homeTaskService.tasks.update(tasks => [...tasks, this.task]);

            Swal.fire({
              title: 'Error',
              text: 'No se pudo eliminar la tarea. Inténtalo de nuevo más tarde.',
              icon: 'error',
              confirmButtonColor: 'var(--color-acento)',
              confirmButtonText: 'Aceptar'
            });
          }
        });

      }
    });
  }
}
