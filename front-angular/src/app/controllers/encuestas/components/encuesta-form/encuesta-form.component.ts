
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
        //this.encuestaService.update(this.encuestaId, this.encuestaForm.value).subscribe(
        //  response => {
        //    alert('Encuesta updated successfully: ' + JSON.stringify(response));
        //    this.router.navigate(['/encuestas']);
        //  },
        //  error => {
        //    alert('Error updating encuesta: ' + JSON.stringify(error));
        //  }
        //);
      } else {
        this.encuestaService.create(tipo, viviendaId, campaniaId).subscribe(
          response => {
            alert('Encuesta created successfully: ' + JSON.stringify(response));
            this.router.navigate(['/encuestas']);
          },
          error => {
            alert('Error creating encuesta: ' + JSON.stringify(error));
          }
        );
      }
    }
  }
}
