// admin/usuarios/admin-usuarios.component.ts
import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {UserService} from '../../../../services/user/user-service';
import {PageResponse} from '../../../../Models/Shared/PageResponse';
import {UserResponse} from '../../../../Models/Users/UserResponse';


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
    // TODO: conectar con endpoint de activar/desactivar
    console.log('Toggle enabled:', user.id, user.enabled);
  }

  deleteUser(id: string) {
    // TODO: conectar con endpoint de borrado
    console.log('Eliminar usuario:', id);
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
