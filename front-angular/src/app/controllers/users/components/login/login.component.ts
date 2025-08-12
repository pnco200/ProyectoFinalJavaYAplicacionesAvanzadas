import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      mail: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const { mail, password } = this.loginForm.value;
      this.authService.login(mail, password).subscribe({
        next: () => {
          Swal.fire('¡Éxito!', 'Login exitoso!', 'success');
          this.cdr.detectChanges(); // Force change detection
          this.router.navigate(['/']); // Volver al home
        },
        error: (err) => {
          Swal.fire('¡Error!', 'Error de login: ' + (err.error || err.message), 'error');
          console.error('Login error:', err);
        }
      });
    }
  }
}
