import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  startDemo(): void {
    this.authService.guestLogin().subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Erro ao iniciar demo', err);
        alert('Não foi possível iniciar demonstração no momento.');
      },
    });
  }
}
