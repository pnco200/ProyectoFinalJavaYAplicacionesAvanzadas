import { environment } from '../../../../../environments/environment';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

  constructor(private http: HttpClient) {}

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
    this.http.post(url, formData).subscribe({
      next: () => {
        this.uploadSuccess = true;
        this.errorMessage = null;
        this.listarReportesPrivados();
      },
      error: (err) => {
        this.uploadSuccess = false;
        this.errorMessage = 'Error al subir el reporte. Por favor, inténtelo de nuevo.';
        console.error(err);
      }
    });
  }

  listarReportesPrivados() {
    const url = `${environment.apiUrl}/reportes/listar/privado`;
    this.http.get<string[]>(url).subscribe({
      next: (data) => (this.reportesPrivados = data ?? []),
      error: (err) => console.error('Error al listar los reportes privados:', err)
    });
  }

  // Helper para encodar SOLO el segmento del nombre
  private encNombre(nombreArchivo: string): string {
    return encodeURIComponent(nombreArchivo);
  }

  descargarReporte(nombreArchivo: string) {
    const url = `${environment.apiUrl}/reportes/descargar/privado/${this.encNombre(nombreArchivo)}`;
    this.http.get(url, { responseType: 'blob', observe: 'response' }).subscribe({
      next: (resp) => {
        const blob = resp.body!;
        // Intentar tomar nombre desde Content-Disposition si viene
        const cd = resp.headers.get('Content-Disposition') ?? '';
        const match = /filename\*?=(?:UTF-8'')?"?([^\";]+)"?/i.exec(cd);
        const filenameFromHeader = match ? decodeURIComponent(match[1]) : nombreArchivo;

        const objUrl = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = objUrl;
        a.download = filenameFromHeader;
        a.click();
        URL.revokeObjectURL(objUrl);
      },
      error: (err) => console.error('Error al descargar el reporte:', err)
    });
  }

  eliminarReporte(nombreArchivo: string) {
    const url = `${environment.apiUrl}/reportes/privado/${this.encNombre(nombreArchivo)}`;
    this.http.delete(url).subscribe({
      next: () => this.listarReportesPrivados(),
      error: (err) => console.error('Error al eliminar el reporte:', err)
    });
  }
}
