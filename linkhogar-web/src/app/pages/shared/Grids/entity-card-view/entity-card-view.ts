import {Component, EventEmitter, Input, OnInit, Output, TemplateRef} from '@angular/core';
import { PageResponse } from '../../../../Models/Shared/PageResponse';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-entity-card-view',
  imports: [CommonModule],
  templateUrl: './entity-card-view.html',
  styleUrl: './entity-card-view.scss',
})
export class EntityCardView implements OnInit{
  @Input({required: true}) pageData!: PageResponse<any> | null;

  @Input() isLoading: boolean = false;

  //Plantilla de como se ve cada tarjeta
  @Input({required: true}) cardTemplate!: TemplateRef<any>;

  //Mensaje de encontrado
  @Input({required: true}) headerMessage!: string;
  @Input({required: true}) headerMessagePlural!: string;

  //Cuando el usuario cambia de pagina
  @Output() pageChange = new EventEmitter<number>();

  ngOnInit(): void {}

  onPageChange(newPage: number){
    if(this.pageData && newPage >= 0 && newPage < this.pageData.totalPages){
      this.pageChange.emit(newPage);
    }
  }

  get currentPage(){
    return this.pageData?.number ?? 0;
  }

  get totalPages(){return this.pageData?.totalPages ?? 0;}

  get hasPrevious(): boolean {
    return this.currentPage > 0;
  }

  get hasNext(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

}
