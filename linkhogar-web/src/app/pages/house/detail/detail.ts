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

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [DecimalPipe, CommonModule, MapView],
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
  currenUser = this.authService.currentUser;

  @Input() previewData: HouseForm | null = null;

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

    const userId = this.authService.currentUser()?.id || null;

    if (this.houseId != null && userId != null) {
      this.userService.addFavouriteHouse(userId, this.houseId).subscribe({
        next: params => {
          this.authService.toggleFavoriteLocal(this.houseId?.toString() || "");
          this.isFavourite.set(true);
          this.isLoading.set(false);
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
}
