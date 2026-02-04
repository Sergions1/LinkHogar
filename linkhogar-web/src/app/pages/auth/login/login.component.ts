import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms'; //usado para que se guarde la info del input sola en la variable
import { AuthService } from '../../../services/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule], //Herramientas usadas por el componente
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  standalone: true //hace que sea un archivo independiente
})
export class LoginComponent {
    loginData = {
      mail: "",
      password: "",
    }

    private authService = inject(AuthService);
    private router = inject(Router);

    onSubmit(){
      this.authService.login(this.loginData).subscribe({
        next: (token) => {
          localStorage.setItem("token", token);
          alert("Inicio de sesion correcto. Token: " + token);
          //Falta redirigir

        },
        error: (error) => {
          alert("Ha ocurrido un error: " + error.message);
        }
      })
    }
}
