import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Coordenadas } from '../../models/coordenadas.model';
import { CoordenadasService } from '../../services/coordenadas.service';

@Component({
  selector: 'app-coordenadas-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './coordenadas-form.component.html',
})
export class CoordenadasFormComponent implements OnInit {

  coordenadaForm: FormGroup;
  isEditMode = false;
  coordenadaId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private coordenadasService: CoordenadasService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.coordenadaForm = this.fb.group({
      latitud: ['', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      longitud: ['', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      barrioId: [null, [Validators.pattern('^[0-9]*$')]],
      zonaId: [null, [Validators.pattern('^[0-9]*$')]]
    });
  }

  ngOnInit(): void {
    this.coordenadaId = this.route.snapshot.params['id'];
    if (this.coordenadaId) {
      this.isEditMode = true;
      this.coordenadasService.find(this.coordenadaId).subscribe(data => {
        if (!Array.isArray(data)) {
          this.coordenadaForm.patchValue(data);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.coordenadaForm.valid) {
      const formData: Coordenadas = this.coordenadaForm.value;
      if (this.isEditMode && this.coordenadaId) {
        this.coordenadasService.update(this.coordenadaId, formData).subscribe(() => {
          this.router.navigate(['/coordenadas/list']);
        });
      } else {
        this.coordenadasService.create(formData).subscribe(() => {
          this.router.navigate(['/coordenadas/list']);
        });
      }
    }
  }
}
