import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ZonaService } from '../../services/zona.service';
import { Coordenadas } from '../../../coordenadas/models/coordenadas.model';
import { Zona } from '../../models/zona.model';

@Component({
  selector: 'app-zona-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './zona-form.component.html',
})
export class ZonaFormComponent implements OnInit {

  zonaForm: FormGroup;
  isEditMode = false;
  zonaId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private zonaService: ZonaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.zonaForm = this.fb.group({
      color: ['', Validators.required],
      barrioId: [null, [Validators.required, Validators.pattern('^[0-9]*$')]],
      coordenadas: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.zonaId = this.route.snapshot.params['id'];
    if (this.zonaId) {
      this.isEditMode = true;
      this.zonaService.findById(this.zonaId).subscribe(data => {
        if (data.length > 0) {
          const zona = data[0];
          this.zonaForm.patchValue({
            color: zona.color,
            barrioId: zona.barrioId
          });
          zona.coordenadas.forEach(coord => {
            this.coordenadas.push(this.createCoordenadaGroup(coord));
          });
        }
      });
    } else {
      // Add initial coordinate fields for new zona
      this.addCoordenada();
      this.addCoordenada();
      this.addCoordenada();
    }
  }

  get coordenadas(): FormArray {
    return this.zonaForm.get('coordenadas') as FormArray;
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
    if (this.zonaForm.valid) {
      const formData: Zona = this.zonaForm.value;
      if (this.isEditMode && this.zonaId) {
        this.zonaService.update(this.zonaId, formData).subscribe(() => {
          this.router.navigate(['/zonas/list']);
        });
      } else {
        this.zonaService.create(formData).subscribe(() => {
          this.router.navigate(['/zonas/list']);
        });
      }
    }
  }
}
