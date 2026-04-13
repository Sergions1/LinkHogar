import {Component, effect, inject, OnInit, signal} from '@angular/core';
import {HouseCardResponse} from '../../../Models/Houses/house-card-response.interface';
import {PageResponse} from '../../../Models/Shared/PageResponse';
import {AuthService} from '../../../services/auth/auth.service';
import {UserService} from '../../../services/user/user-service';
import Swal from 'sweetalert2';
import {HouseCard} from '../../shared/house-card/house-card';
import {EntityCardView} from '../../shared/Grids/entity-card-view/entity-card-view';
import {UserResponse} from '../../../Models/Users/UserResponse';

@Component({
  selector: 'app-favourites',
  imports: [
    HouseCard,
    EntityCardView
  ],
  templateUrl: './favourites.html',
  styleUrl: './favourites.scss',
})
export class Favourites{
  private authService = inject(AuthService);
  private userService = inject(UserService);

  houses = signal<PageResponse<HouseCardResponse> | null>(null);
  isLoading = signal(false);
  currentUser: UserResponse | null = null;

  constructor() {
    // El effect se ejecuta automáticamente al iniciar y CADA VEZ que el usuario cambia
    effect(() => {
      const user = this.authService.currentUser();

      if (user) {
        this.currentUser = user;
        this.loadFavourites(0);
      }
    });
  }
  loadFavourites(page: number){
    const user = this.currentUser;

    if(user == null){
      return;
    }

    this.isLoading.set(true);
    this.userService.getPaginatedFavourites(user.id, page).subscribe({
      next: (response) => {
        this.houses.set(response);
        this.isLoading.set(false);
      },
      error: (err) => {
        Swal.fire({
          title: 'Error',
          text: 'No se pudo buscar sus favoritos.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
          confirmButtonColor: 'var(--color-acento)'
        });
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(newPage: number) {
    this.loadFavourites(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
