import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  isLoading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  forgotForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  onSubmit(): void {
    if (this.forgotForm.valid) {
      this.isLoading = true;
      this.successMessage = null;
      this.errorMessage = null;

      const email = this.forgotForm.value.email.trim();

      this.authService.forgotPassword(email).subscribe({
        next: (response) => {
          this.successMessage = response;
          this.isLoading = false;
          this.forgotForm.reset();
        },
        error: (err) => {
          console.error('Erro ao solicitar recuperação', err);
          this.errorMessage =
            'Ocorreu um erro ao processar sua solicitação. Tente novamente.';
          this.isLoading = false;
        },
      });
    }
  }
}
