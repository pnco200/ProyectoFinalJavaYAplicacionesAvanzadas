import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home-page',
  imports: [CommonModule],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css'
})
export class HomePage implements OnInit {
  reportesPublicos: string[] = [];

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.listarReportesPublicos();
  }

  listarReportesPublicos() {
    const url = 'http://localhost:8080/ProyectoFinal/api/reportes/listar/publico';
    this.http.get<string[]>(url).subscribe(
      (data) => {
        this.reportesPublicos = data;
      },
      (error) => {
        console.error('Error al listar los reportes públicos:', error);
      }
    );
  }

  descargarReporte(nombreArchivo: string) {
    const url = `http://localhost:8080/ProyectoFinal/api/reportes/descargar/publico/${nombreArchivo}`;
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
}
