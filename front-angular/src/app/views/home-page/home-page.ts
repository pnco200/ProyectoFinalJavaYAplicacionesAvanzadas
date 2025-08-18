import { environment } from '../../../environments/environment';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home-page',
  imports: [CommonModule],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css'
})
export class HomePage implements OnInit {
  reportesPublicos: string[] = [];
  isAdmin: boolean = false;

  constructor(private http: HttpClient, private authService: AuthService) { }

  ngOnInit() {
    this.listarReportesPublicos();
    const currentUser = this.authService.currentUserValue;
    if (currentUser && currentUser.roles) {
      this.isAdmin = currentUser.roles.includes('ADMINISTRADOR');
    }
  }

  listarReportesPublicos() {
    const url = `${environment.apiUrl}/reportes/listar/publico`;
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
    const url = `${environment.apiUrl}/reportes/descargar/publico/${nombreArchivo}`;
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
    const url = `${environment.apiUrl}/reportes/publico/${nombreArchivo}`;
    this.http.delete(url).subscribe(
      () => {
        this.listarReportesPublicos();
      },
      (error) => {
        console.error('Error al eliminar el reporte:', error);
      }
    );
  }
}
