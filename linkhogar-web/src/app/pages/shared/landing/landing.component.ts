import {Component, inject, signal} from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-landing',
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  private router = inject(Router);

  searchQuery = signal('');

  onInput(event: Event) {
    const valor = (event.target as HTMLInputElement).value;
    this.searchQuery.set(valor);
  }

  search() {
    const query = this.searchQuery().trim();
    if (query) {
      this.router.navigate(['/explore'], { queryParams: { q: query } });
    }
  }
}
