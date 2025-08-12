
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';
import { Renglon } from '../../models/renglon.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-renglon-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './renglon-form.component.html',
})
export class RenglonFormComponent implements OnInit {

  renglonForm: FormGroup;
  isEditMode = false;
  renglonId: number | undefined;

  constructor(
    private fb: FormBuilder,
    private renglonService: RenglonService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.renglonForm = this.fb.group({
      valor: ['', Validators.required],
      tipo: ['', Validators.required],
      encuestaId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.renglonId = +id;
      this.renglonService.findById(this.renglonId).subscribe(data => {
        this.renglonForm.patchValue(data);
      });
    }
  }

  onSubmit(): void {
    if (this.renglonForm.valid) {
      if (this.isEditMode && this.renglonId) {
        this.renglonService.update({ id: this.renglonId, ...this.renglonForm.value }).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Renglón actualizado con éxito.', 'success');
            this.router.navigate(['/renglones']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al actualizar el renglón.', 'error');
          }
        );
      } else {
        this.renglonService.create(this.renglonForm.value).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Renglón creado con éxito.', 'success');
            this.router.navigate(['/renglones']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al crear el renglón.', 'error');
          }
        );
      }
    }
  }
}
