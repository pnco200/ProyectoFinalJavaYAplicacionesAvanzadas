
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EncuestaService } from '../../services/encuesta.service';
import { Encuesta, TipoEncuesta } from '../../models/encuesta.model';

@Component({
  selector: 'app-encuesta-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './encuesta-form.component.html',
})
export class EncuestaFormComponent implements OnInit {

  encuestaForm: FormGroup;
  isEditMode = false;
  encuestaId: number | undefined;
  tiposEncuesta = Object.values(TipoEncuesta);

  constructor(
    private fb: FormBuilder,
    private encuestaService: EncuestaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.encuestaForm = this.fb.group({
      tipo: ['', Validators.required],
      viviendaId: [null],
      campaniaId: [null]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.encuestaId = +id;
      this.encuestaService.findById(this.encuestaId).subscribe(data => {
        this.encuestaForm.patchValue(data);
      });
    }
  }

  onSubmit(): void {
    if (this.encuestaForm.valid) {
      const { tipo, viviendaId, campaniaId } = this.encuestaForm.value;
      if (this.isEditMode && this.encuestaId) {
        // Update logic not implemented as per backend controller
        // this.encuestaService.update(this.encuestaId, this.encuestaForm.value).subscribe(() => {
        //   this.router.navigate(['/encuestas']);
        // });
        console.error('Update not supported by the backend API');
        this.router.navigate(['/encuestas']);

      } else {
        this.encuestaService.create(tipo, viviendaId, campaniaId).subscribe(() => {
          this.router.navigate(['/encuestas']);
        });
      }
    }
  }
}
