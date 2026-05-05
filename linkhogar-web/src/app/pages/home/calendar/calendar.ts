import {Component, computed, effect, inject, signal} from '@angular/core';
import {AuthService} from '../../../services/auth/auth.service';
import {EventService} from '../../../services/event/event-service';
import {CalendarOptions, EventInput} from '@fullcalendar/core';
import {HomeTaskService} from '../../../services/homeTask/home-task-service';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import {FullCalendarModule} from '@fullcalendar/angular';
import {CreateEventModal} from './create-event-modal/create-event-modal';
import {ViewEventModal} from './view-event-modal/view-event-modal';
import {HomeEventResponse} from '../../../Models/event/eventModel';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-calendar',
  imports: [
    FullCalendarModule,
    CreateEventModal,
    ViewEventModal
  ],
  templateUrl: './calendar.html',
  styleUrl: './calendar.scss',
})
export class Calendar {
  private authService = inject(AuthService);
  private eventService = inject(EventService);
  private taskService = inject(HomeTaskService);
  private route = inject(ActivatedRoute);

  homeId: string | undefined;
  myUserId: string | undefined;
  myUserName: string | undefined;

  // Control del modal
  showCreateModal = signal<boolean>(false);
  selectedDate = signal<string | null>(null);

  //Control del modal de vista y edición ---
  showViewModal = signal<boolean>(false);
  selectedEvent = signal<HomeEventResponse | null>(null);
  eventToEdit = signal<HomeEventResponse | null>(null);

  // Creamos un Signal "computado" que escucha los eventos y las tareas,
  // y los fusiona en el formato exacto que necesita FullCalendar
  calendarEvents = computed<EventInput[]>(() => {
    const rawEvents = this.eventService.homeEvents();
    const rawTasks = this.taskService.tasks() ? this.taskService.tasks() : [];

    // Mapear Eventos (Verdes)
    const formattedEvents: EventInput[] = rawEvents.map(e => ({
      id: e.id,
      title: e.title,
      start: e.startDate,
      end: e.endDate,
      allDay: e.allDay,
      backgroundColor: '#28a745', // Verde para eventos
      borderColor: '#28a745',
      classNames: ['cursor-pointer'],
      extendedProps: { type: 'EVENT', description: e.description }
    }));

    // Mapear Tareas (Azules) - Solo las que tienen fecha
    const formattedTasks: EventInput[] = rawTasks
      .filter(t => t.startDate || t.dueDate) // Filtramos las que no tienen fecha
      .map(t => ({
        id: t.id,
        title: `📝 ${t.title} (${t.assignedUserName || 'Sin asignar'})`,
        start: t.dueDate || t.startDate, // Usamos due date, o start date
        allDay: true, // Las tareas suelen ser de dia completo
        backgroundColor: '#0d6efd', // Azul para tareas
        borderColor: '#0d6efd',
        classNames: ['cursor-default'],
        extendedProps: { type: 'TASK', status: t.status }
      }));

    return [...formattedEvents, ...formattedTasks];
  });

  // Configuración principal de FullCalendar
  calendarOptions = signal<CalendarOptions>({
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    locale: 'es',
    events: this.calendarEvents(),

    // Al hacer clic en un día vacío
    dateClick: (arg) => this.handleDateClick(arg),

    // Al hacer clic en un evento/tarea
    eventClick: (arg) => this.handleEventClick(arg)
  });

  constructor() {
    this.route.queryParams.subscribe(params => {
      if (params['modal'] === 'create') {
        this.showCreateModal.set(true);
      }
    });

    // Escuchamos cambios en currentUser para cargar los datos
    effect(() => {
      const user = this.authService.currentUser();
      if(user?.id){
        this.myUserId = user.id;
        this.myUserName = user.firstName + ' ' + user.lastName;
      }
      if (user?.homeId) {
        this.homeId = user.homeId;
        this.loadCalendarData(user.homeId);
      }
    });

    //Actualizar el calendario cuando cambian los datos computados
    effect(() => {
      this.calendarOptions.update(options => ({
        ...options,
        events: this.calendarEvents()
      }));
    });
  }

  loadCalendarData(homeId: string) {
    this.eventService.getHomeEvents(homeId).subscribe();

    this.taskService.getTasksByHome(homeId).subscribe({
      error: (err) => console.error('Error cargando tareas en calendario', err)
    });
  }

  handleDateClick(arg: any) {
    this.selectedDate.set(arg.dateStr || null);
    this.showCreateModal.set(true);
  }

  handleEventClick(arg: any) {
    const isTask = arg.event.extendedProps.type === 'TASK';
    const clickedId = arg.event.id;

    if (!isTask) {
      const fullEvent = this.eventService.homeEvents().find(e => e.id === clickedId);
      if (fullEvent) {
        this.selectedEvent.set(fullEvent);
        this.showViewModal.set(true);
      }
    }
  }

  // Se ejecuta cuando el modal emite que ha creado un evento
  onEventCreated() {
    this.closeCreateModal();
    if (this.homeId) {
      // Recargamos el calendario para ver el nuevo evento
      this.loadCalendarData(this.homeId);
    }
  }

  //Abre el modal de edición recibiendo los datos del evento
  openEditModal(event: HomeEventResponse) {
    this.showViewModal.set(false);
    this.eventToEdit.set(event);
    this.showCreateModal.set(true);
  }

  //Limpia todas las variables al cerrar el modal
  closeCreateModal() {
    this.showCreateModal.set(false);
    this.eventToEdit.set(null);
    this.selectedDate.set(null);
  }
}
