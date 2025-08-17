import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-solicitar-reporte',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './solicitar-reporte.component.html',
  styleUrl: './solicitar-reporte.component.css'
})
export class SolicitarReporteComponent {
  reporteInput: string = '';

  solicitarReporte() {
    Swal.fire({
      icon: 'success',
      title: 'Solicitud Creada',
      text: `Se ha creado la solicitud para: ${this.reporteInput}`,
      confirmButtonText: 'Aceptar'
    });
    this.reporteInput = '';
  }
}
