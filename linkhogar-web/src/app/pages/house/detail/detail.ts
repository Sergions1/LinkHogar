import {Component, inject, Input, OnInit, signal} from '@angular/core';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';
import {ActivatedRoute, Router} from '@angular/router';
import {HouseService} from '../../../services/house/house-service';
import {CommonModule, DecimalPipe} from '@angular/common';
import {HouseForm} from '../create/create';
import {MapView} from '../../shared/map-view/map-view';
import {UserService} from '../../../services/user/user-service';
import {AuthService} from '../../../services/auth/auth.service';
import Swal from 'sweetalert2';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [DecimalPipe, CommonModule, MapView, FormsModule],
  templateUrl: './detail.html',
  styleUrl: './detail.scss',
})
export class Detail implements OnInit {
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);

  house = signal<HouseResponse | null>(null);
  isLoading = signal(false);
  houseId: string | null = null;
  currentImageIndex = 0; // 👈
  isFavourite = signal(false);
  currentUser = this.authService.currentUser;

  @Input() previewData: HouseForm | null = null;

  reportData = {
    reason: '',
    description: ''
  };

  ngOnInit() {
    if (this.previewData) return;
    this.loadHouse();
  }

  prevImage(images: string[]) {
    this.currentImageIndex = this.currentImageIndex === 0
      ? images.length - 1
      : this.currentImageIndex - 1;
  }

  nextImage(images: string[]) {
    this.currentImageIndex = this.currentImageIndex === images.length - 1
      ? 0
      : this.currentImageIndex + 1;
  }

  goToImage(index: number) {
    this.currentImageIndex = index;
  }

  loadHouse() {
    this.isLoading.set(true);
    this.route.paramMap.subscribe(param => {
      this.houseId = param.get('id');
    });
    if (this.houseId != null) {
      this.houseService.getHouseById(this.houseId).subscribe({
        next: params => {
          this.house.set(params);
          this.currentImageIndex = 0; // 👈 reset al cargar
          this.isFavourite.set(this.authService.isFavorite(this.houseId?.toString() || ''));
          this.isLoading.set(false);
        },
        error: err => {
          console.log(err);
          this.isLoading.set(false);
        }
      });
    }
  }

  addFavourite(){

    if(this.authService.currentUser == null){
      this.router.navigate(['/login']);
      return;
    }

    const userId = this.currentUser()?.id || null;

    if (this.houseId != null && userId != null) {
      this.userService.addFavouriteHouse(userId, this.houseId).subscribe({
        next: params => {
          this.authService.toggleFavoriteLocal(this.houseId?.toString() || "");
          this.isFavourite.set(true);
        },
        error: err => {
          Swal.fire({
            title: 'Error',
            text: 'No se pudo añadir a favoritos.',
            icon: 'error',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }else{
      this.router.navigate(["/login"]);
    }
  }

  deleteFavourite(){
    const userId = this.currentUser()?.id || null;

    if (this.houseId != null && userId != null) {
      this.userService.deleteFavouriteHosue(userId, this.houseId).subscribe({
        next: params => {
          this.authService.toggleFavoriteLocal(this.houseId?.toString() || "");
          this.isFavourite.set(false);
        },
        error: err => {
          Swal.fire({
            title: 'Error',
            text: 'No se pudo eliminar de favoritos.',
            icon: 'error',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }
  }

  report(){
    if (!this.currentUser()) {
      document.getElementById('closeReportModal')?.click(); // Cierra el modal
      this.router.navigate(['/login']);
      return;
    }

    if (!this.houseId || !this.reportData.reason) return;

    this.isLoading.set(true);

    this.houseService.reportHouse(this.houseId, this.reportData.reason, this.reportData.description).subscribe({
      next: () => {
        this.isLoading.set(false);
        document.getElementById('closeReportModal')?.click(); // Cierra el modal
        this.reportData = { reason: '', description: '' }; // Resetea el formulario

        Swal.fire({
          title: 'Denuncia enviada',
          text: 'Nuestro equipo revisará el anuncio lo antes posible.',
          icon: 'success',
          confirmButtonText: 'Aceptar',
          confirmButtonColor: 'var(--color-acento)'
        }).then((result) => {
          // Comprobamos si el usuario hizo clic en "Aceptar"
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
      },
      error: (err) => {
        this.isLoading.set(false);
        document.getElementById('closeReportModal')?.click();

        Swal.fire({
          title: 'Error',
          text: 'No se pudo enviar la denuncia. Inténtalo de nuevo más tarde.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
          confirmButtonColor: 'var(--color-acento)'
        });
      }
    });
  }
}
