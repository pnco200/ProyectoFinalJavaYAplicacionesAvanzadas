import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RenglonService } from '../../../renglones/services/renglon.service';

interface Filter {
  tipo: string;
  value: string;
  tipo_encuesta: string;
}

@Component({
  selector: 'app-campania-filters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './campania-filters.component.html',
  //styleUrls: ['./campania-filters.component.css']
})
export class CampaniaFiltersComponent implements OnInit {
  @Input() campaniaId!: number;
  tipos: [string, string][] = [];
  valores: { [key: string]: string[] } = {};
  filters: Filter[] = [{ tipo: '', value: '', tipo_encuesta: '' }];

  constructor(private renglonService: RenglonService) { }

  ngOnInit(): void {
    this.renglonService.getTipos().subscribe(data => {
      this.tipos = data;
    });
  }

  addFilter(): void {
    this.filters.push({ tipo: '', value: '', tipo_encuesta: '' });
  }

  removeFilter(index: number): void {
    this.filters.splice(index, 1);
  }

  onTipoChange(filter: Filter): void {
    if (filter.tipo) {
      const selectedTipo = this.tipos.find(t => t[0] === filter.tipo);
      if (selectedTipo) {
        filter.tipo_encuesta = selectedTipo[1];
        this.renglonService.getValores(filter.tipo).subscribe(data => {
          this.valores[filter.tipo] = data;
        });
      }
    }
  }

  getAvailableTipos(currentFilter: Filter): [string, string][] {
    const selectedTipos = this.filters.map(f => f.tipo).filter(t => t !== currentFilter.tipo);
    return this.tipos.filter(t => !selectedTipos.includes(t[0]));
  }

  getAvailableValores(filter: Filter): string[] {
    const selectedValues = this.filters.filter(f => f.tipo === filter.tipo).map(f => f.value).filter(v => v !== filter.value);
    return (this.valores[filter.tipo] || []).filter(v => !selectedValues.includes(v));
  }

  saveFilters(): void {
    const data = {
      campaniaId: this.campaniaId,
      filters: this.filters
    };
      localStorage.setItem('filtro', JSON.stringify(this.filters));
      console.log('Filters saved successfully to local storage');
  }
}
