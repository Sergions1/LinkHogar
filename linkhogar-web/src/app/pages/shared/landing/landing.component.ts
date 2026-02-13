import { Component, inject } from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-landing.component',
  imports: [RouterModule, FormsModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  private router = inject(Router);

  searchCity: String = "";

  search(){
    if (this.searchCity.trim()){
      this.router.navigate(["/explore"], { queryParams: { City: this.searchCity } });
    }
  }
}
