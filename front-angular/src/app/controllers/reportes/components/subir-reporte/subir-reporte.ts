import { environment } from '../../../../../environments/environment';
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-subir-reporte',
  templateUrl: './subir-reporte.html',
  styleUrls: ['./subir-reporte.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class SubirReporteComponent implements OnInit {
  selectedFile: File | null = null;
  visibilidad: 'publico' | 'privado' = 'publico';
  uploadSuccess = false;
  errorMessage: string | null = null;
  reportesPrivados: string[] = [];

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.listarReportesPrivados();
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  subirReporte() {
    if (!this.selectedFile) {
      this.errorMessage = 'Por favor, seleccione un archivo.';
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile, this.selectedFile.name);

    const url = `${environment.apiUrl}/reportes/upload/${this.visibilidad}`;

    this.http.post(url, formData).subscribe(
      (response) => {
        this.uploadSuccess = true;
        this.errorMessage = null;
        this.listarReportesPrivados(); // Actualizar la lista después de subir
      },
      (error) => {
        this.uploadSuccess = false;
        this.errorMessage = 'Error al subir el reporte. Por favor, inténtelo de nuevo.';
        console.error(error);
      }
    );
  }

  listarReportesPrivados() {
    const url = `${environment.apiUrl}/reportes/listar/privado`;
    this.http.get<string[]>(url).subscribe(
      (data) => {
        this.reportesPrivados = data;
      },
      (error) => {
        console.error('Error al listar los reportes privados:', error);
      }
    );
  }

  descargarReporte(nombreArchivo: string) {
    const url = `${environment.apiUrl}/reportes/descargar/privado/${nombreArchivo}`;
    this.http.get(url, { responseType: 'blob' }).subscribe(
      (data) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error al descargar el reporte:', error);
      }
    );
  }

  eliminarReporte(nombreArchivo: string) {
    const url = `${environment.apiUrl}/reportes/privado/${nombreArchivo}`;
    this.http.delete(url).subscribe(
      () => {
        this.listarReportesPrivados();
      },
      (error) => {
        console.error('Error al eliminar el reporte:', error);
      }
    );
  }
}

