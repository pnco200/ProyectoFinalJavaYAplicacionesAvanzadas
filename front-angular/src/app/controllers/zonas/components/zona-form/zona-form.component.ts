import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ZonaService } from '../../services/zona.service';
import { Coordenadas } from '../../../coordenadas/models/coordenadas.model';
import { Zona } from '../../models/zona.model';
import { Barrio } from '../../../barrios/models/barrio.model';
import { BarrioService } from '../../../barrios/services/barrio.service';

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
  barrios: Barrio[] = [];

  constructor(
    private fb: FormBuilder,
    private zonaService: ZonaService,
    private barrioService: BarrioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.zonaForm = this.fb.group({
      color: ['', Validators.required],
      barrio: [null, Validators.required],
      coordenadas: this.fb.array([])
    });
  }
  compareById = (a: any, b: any) => a && b && a.id === b.id;

  ngOnInit(): void {
    this.loadBarrios();
    this.zonaId = this.route.snapshot.params['id'];
    if (this.zonaId) {
      this.isEditMode = true;
      this.zonaService.findById(this.zonaId).subscribe(data => {
        if (data.length > 0) {
          const zona = data[0];
          this.zonaForm.patchValue({
            color: zona.color,
          });
          // Find the barrio object from the loaded barrios array
          console.log('Zona data:', zona);
          if (zona.barrioId) {
            this.barrioService.getAll().subscribe(data => {
              console.log('Barrios data:', data);
            const selectedBarrio = data.find(b => b.id == zona.barrioId);
                          console.log('Selected Barrio1:', selectedBarrio);
            if (selectedBarrio) {
              console.log('Selected Barrio2:', selectedBarrio);
              this.zonaForm.get('barrio')?.setValue(selectedBarrio);
            }
            });
          }
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

  loadBarrios(): void {
    this.barrioService.getAll().subscribe(data => {
      this.barrios = data;
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
      const selectedBarrio = this.zonaForm.get('barrio')?.value;

      if (selectedBarrio) {
        formData.barrioId = selectedBarrio.id;
      }

      if (this.isEditMode && this.zonaId) {
        this.zonaService.update(this.zonaId, formData).subscribe(
          response => {
            alert('Zona updated successfully: ' + JSON.stringify(response));
            this.router.navigate(['/zonas/list']);
          },
          error => {
            alert('Error updating zona: ' + JSON.stringify(error));
          }
        );
      } else {
        this.zonaService.create(formData).subscribe(
          response => {
            alert('Zona created successfully: ' + JSON.stringify(response));
            this.router.navigate(['/zonas/list']);
          },
          error => {
            alert('Error creating zona: ' + JSON.stringify(error));
          }
        );
      }
    }
  }
}
