import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { AuthService } from './services/auth.service';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, CommonModule],
  template: `
    <app-navbar *ngIf="showNavbar"></app-navbar>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`.main-content { min-height: calc(100vh - 64px); }`]
})
export class App {
  showNavbar = false;

  constructor(public auth: AuthService, private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const hiddenRoutes = ['/', '/about', '/login', '/register'];
      this.showNavbar = !hiddenRoutes.includes(event.url);
    });
  }
}
