// admin/usuarios/admin-usuarios.component.ts
import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {UserService} from '../../../../services/user/user-service';
import {PageResponse} from '../../../../Models/Shared/PageResponse';
import {UserResponse} from '../../../../Models/Users/UserResponse';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.html',
  styleUrls: ['./admin-users.scss']
})
export class AdminUsersComponent implements OnInit {
  private userService = inject(UserService);

  users = signal<PageResponse<UserResponse> | null>(null);
  isLoading = signal(false);
  currentPage = signal(0);
  readonly pageSize = 10;

  // Filtros
  searchText = '';
  selectedRole = '';
  selectedEnabled: boolean | undefined = undefined;

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading.set(true);
    this.userService.getAllUsers(
      this.currentPage(),
      this.pageSize,
      this.searchText || undefined,
      this.selectedRole || undefined,
      this.selectedEnabled
    ).subscribe({
      next: (data) => {
        this.users.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.isLoading.set(false);
      }
    });
  }

  applyFilters() {
    this.currentPage.set(0);
    this.loadUsers();
  }

  clearFilters() {
    this.searchText = '';
    this.selectedRole = '';
    this.selectedEnabled = undefined;
    this.currentPage.set(0);
    this.loadUsers();
  }

  goToPage(page: number) {
    this.currentPage.set(page);
    this.loadUsers();
  }

  toggleEnabled(user: UserResponse) {
    const accion = user.enabled ? 'desactivar' : 'activar';
    const accionPasado = user.enabled ? 'desactivado' : 'activado';

    Swal.fire({
      title: `¿${accion.charAt(0).toUpperCase() + accion.slice(1)} usuario?`,
      text: `Vas a ${accion} a ${user.firstName} ${user.lastName}.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: `Sí, ${accion}`,
      cancelButtonText: 'Cancelar',
      confirmButtonColor: 'var(--color-acento)',
      cancelButtonColor: '#6c757d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.toggleUserEnabled(user.id).subscribe({
          next: () => {
            this.users.update(page => {
              if (!page) return page;
              return {
                ...page,
                content: page.content.map(u =>
                  u.id === user.id ? { ...u, enabled: !u.enabled } : u
                )
              };
            });

            Swal.fire({
              title: '¡Hecho!',
              text: `Usuario ${accionPasado} correctamente.`,
              icon: 'success',
              confirmButtonColor: 'var(--color-acento)',
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Error al cambiar estado');
            Swal.fire({
              title: 'Error',
              text: 'No se pudo cambiar el estado del usuario.',
              icon: 'error',
              confirmButtonColor: 'var(--color-acento)'
            });
          }
        });
      }
    });
  }

  deleteUser(user: UserResponse) {
    Swal.fire({
      title: '¿Eliminar usuario?',
      text: `Vas a eliminar a ${user.firstName} ${user.lastName}. Esta acción no se puede deshacer.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.users.update(page => {
              if (!page) return page;
              return {
                ...page,
                content: page.content.filter(u => u.id !== user.id),
                totalElements: page.totalElements - 1
              };
            });

            Swal.fire({
              title: '¡Eliminado!',
              text: `${user.firstName} ${user.lastName} ha sido eliminado correctamente.`,
              icon: 'success',
              confirmButtonColor: 'var(--color-acento)',
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Error al eliminar usuario:', err);
            Swal.fire({
              title: 'Error',
              text: 'No se pudo eliminar el usuario.',
              icon: 'error',
              confirmButtonColor: 'var(--color-acento)'
            });
          }
        });
      }
    });
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'LinkHogar': return 'bg-danger';
      case 'Admin': return 'bg-warning text-dark';
      case 'Owner': return 'bg-primary';
      default: return 'bg-secondary';
    }
  }

  get usersList() { return this.users()?.content ?? []; }
  get totalPages() { return this.users()?.totalPages ?? 0; }
  get isFirstPage() { return this.users()?.first ?? true; }
  get isLastPage() { return this.users()?.last ?? true; }
  get totalElements() { return this.users()?.totalElements ?? 0; }
  get getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
