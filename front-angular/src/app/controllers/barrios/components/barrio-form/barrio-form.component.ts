import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { BarrioService } from '../../services/barrio.service';
import { Barrio } from '../../models/barrio.model';
import { Coordenadas } from '../../../coordenadas/models/coordenadas.model';
@Component({
  selector: 'app-barrio-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './barrio-form.component.html',
})
export class BarrioFormComponent implements OnInit {

  barrioForm: FormGroup;
  isEditMode = false;
  barrioId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private barrioService: BarrioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.barrioForm = this.fb.group({
      name: ['', Validators.required],
      coordenadas: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.barrioId = this.route.snapshot.params['id'];
    if (this.barrioId) {
      this.isEditMode = true;
      this.barrioService.getAll(this.barrioId).subscribe(data => {
        if (data.length > 0) {
          const barrio = data[0];
          this.barrioForm.patchValue({
            name: barrio.name
          });
          barrio.coordenadas.forEach(coord => {
            this.coordenadas.push(this.createCoordenadaGroup(coord));
          });
        }
      });
    } else {
      // Add initial coordinate fields for new barrio
      this.addCoordenada();
      this.addCoordenada();
      this.addCoordenada();
    }
  }

  get coordenadas(): FormArray {
    return this.barrioForm.get('coordenadas') as FormArray;
  }

  createCoordenadaGroup(coord?: Coordenadas): FormGroup {
    return this.fb.group({
      latitud: [coord ? coord.latitud : '', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      longitud: [coord ? coord.longitud : '', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]]
    });
  }

  addCoordenada(): void {
    this.coordenadas.push(this.createCoordenadaGroup());
  }

  removeCoordenada(index: number): void {
    this.coordenadas.removeAt(index);
  }

  onSubmit(): void {
    if (this.barrioForm.valid) {
      const formData: Barrio = this.barrioForm.value;
      if (this.isEditMode && this.barrioId) {
        this.barrioService.update(this.barrioId, formData).subscribe(() => {
          this.router.navigate(['/barrios/list']);
        });
      } else {
        this.barrioService.create(formData).subscribe(() => {
          this.router.navigate(['/barrios/list']);
        });
      }
    }
  }
}
