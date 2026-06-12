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
import {ChatService} from '../../../services/chat/chat-service';
import {GenderPipe} from '../../../pipes/GenderPipe';
import {OccupationPipe} from '../../../pipes/OccupationPipe';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [DecimalPipe, CommonModule, MapView, FormsModule, GenderPipe, OccupationPipe],
  templateUrl: './detail.html',
  styleUrl: './detail.scss',
})
export class Detail implements OnInit {
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);
  private chatService = inject(ChatService)

  house = signal<HouseResponse | null>(null);
  isLoading = signal(false);
  houseId: string | null = null;
  currentImageIndex = 0; // 👈
  isFavourite = signal(false);
  currentUser = this.authService.currentUser;
  isToggling = signal(false); //Señal para controlar bloqueo de carrera

  activeRoomIndex = signal<number>(0);
  roomImageIndices = signal<Record<string, number>>({});

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
          this.currentImageIndex = 0;
          this.activeRoomIndex.set(0);
          this.isFavourite.set(this.authService.isFavorite(this.houseId?.toString() || ''));
          this.isLoading.set(false);

          if (this.house()?.publicationStatus === 'ARCHIVED') {

            // 🌟 CORRECCIÓN 2: Evaluamos el rol asegurando que currentUser ya no sea null
            const usuario = this.currentUser();
            const autorized = usuario && ['Admin', 'LinkHogar'].includes(usuario.role || '');

            if (!autorized) {
              this.router.navigate(['notFound']);
            }
          }
        },
        error: err => {
          console.log(err);
          this.isLoading.set(false);
          this.router.navigate(['notFound']);
        }
      });
    }
  }

  toggleFavourite() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    if (this.isToggling()) return;

    const userId = this.currentUser()?.id;
    if (!this.houseId || !userId) return;

    //Guardamos el estado anterior por si falla
    const wasFavourite = this.isFavourite();

    this.isToggling.set(true)

    this.isFavourite.set(!wasFavourite);
    this.authService.toggleFavoriteLocal(this.houseId);

    if (!wasFavourite) {
      // Si antes NO era favorito, lo añadimos
      this.userService.addFavouriteHouse(userId, this.houseId).subscribe({
        next: () => {
          this.isToggling.set(false)
        },
        error: (err) => this.rollbackFavourite(wasFavourite, this.houseId!)
      });
    } else {
      // Si antes SÍ era favorito, lo quitamos
      this.userService.deleteFavouriteHosue(userId, this.houseId).subscribe({
        next: () => {
          this.isToggling.set(false)
        },
        error: (err) => this.rollbackFavourite(wasFavourite, this.houseId!)
      });
    }
  }

  // Función auxiliar para revertir los cambios si el servidor falla
  private rollbackFavourite(previousState: boolean, houseId: string) {
    this.isFavourite.set(previousState);
    this.authService.toggleFavoriteLocal(houseId); // Revertimos en la caché local
    this.isToggling.set(false)

    Swal.fire({
      title: 'Error de conexión',
      text: 'No se pudo actualizar favoritos. Inténtalo de nuevo más tarde.',
      icon: 'error',
      confirmButtonText: 'Aceptar',
      confirmButtonColor: 'var(--color-acento)'
    });
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


  async onInterested()
  {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.authService.isLoggedIn()) {
      Swal.fire({
        icon: 'warning',
        title: 'Inicia sesión',
        text: 'Debes estar registrado para contactar con el propietario.',
        confirmButtonColor: 'var(--color-acento)'
      });
      return;
    }

    // 2. Comprobamos que no sea el dueño de su propia casa
    const myId = this.authService.currentUser()?.id;
    if (myId === this.house()?.owner.id) {
      Swal.fire({
        icon: 'info',
        title: 'Este es tu anuncio',
        text: 'No puedes iniciar un chat contigo mismo.',
        confirmButtonColor: 'var(--color-primario)'
      });
      return;
    }

    // 3. Abrimos el modal de SweetAlert para pedir el mensaje inicial
    const { value: initialMessage } = await Swal.fire({
      title: 'Contactar al propietario',
      input: 'textarea',
      inputLabel: 'Escribe tu primer mensaje:',
      inputPlaceholder: 'Hola, estoy muy interesado en este alquiler...',
      inputAttributes: {
        'aria-label': 'Escribe tu primer mensaje'
      },
      showCancelButton: true,
      confirmButtonText: 'Enviar mensaje',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: 'var(--color-acento)',
      inputValidator: (value) => {
        if (!value || value.trim().length === 0) {
          return '¡Debes escribir un mensaje para empezar!';
        }
        return null;
      }
    });

    // 4. Si el usuario escribió algo y le dio a Enviar
    if (initialMessage) {
      // Mostramos un loading de SweetAlert mientras llama al backend
      Swal.fire({
        title: 'Enviando...',
        text: 'Iniciando conversación',
        allowOutsideClick: false,
        didOpen: () => {
          Swal.showLoading();
        }
      });

      // 5. Llamamos a tu Backend (al POST /api/chats/initiate)
      this.chatService.initiateChat(this.house()?.id || "", this.house()?.owner.id || "", initialMessage).subscribe({
        next: (response) => {
          Swal.close();
          // REDIRECCIÓN MÁGICA: Lo mandamos al buzón, directamente a este chat
          this.router.navigate(['/messages', response.chatId]);
        },
        error: (err) => {
          console.error(err);
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Hubo un problema al enviar el mensaje. Inténtalo de nuevo.',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }
  }

  setActiveRoom(index: number) {
    this.activeRoomIndex.set(index);
  }

  prevRoomImage(roomId: string, imagesLength: number) {
    this.roomImageIndices.update(indices => {
      const currentIndex = indices[roomId] || 0;
      const newIndex = currentIndex === 0 ? imagesLength - 1 : currentIndex - 1;
      return { ...indices, [roomId]: newIndex };
    });
  }

  nextRoomImage(roomId: string, imagesLength: number) {
    this.roomImageIndices.update(indices => {
      const currentIndex = indices[roomId] || 0;
      const newIndex = currentIndex === imagesLength - 1 ? 0 : currentIndex + 1;
      return { ...indices, [roomId]: newIndex };
    });
  }

  goToRoomImage(roomId: string, index: number) {
    this.roomImageIndices.update(indices => ({ ...indices, [roomId]: index }));
  }

  getRoomImageIndex(roomId: string): number {
    return this.roomImageIndices()[roomId] || 0;
  }

  protected readonly String = String;
}
