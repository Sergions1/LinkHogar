import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-select-input',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './select-input.html',
  styleUrl: './select-input.scss',
})
export class SelectInput {
  @Input() Label: string  = "";
  @Input() Placeholder?: string;

  @Input() items: any[] = [];
  @Input() valueSelector: (item: any) => any = (item) => item;
  @Input() textSelector: (item: any) => string = (item) => String(item);

  @Input() selectedValue: any;
  @Output() selectedValueChange = new EventEmitter<any>();


  @Input() cssClass?: string;
  @Input() validationClass?: string;
  @Input() additionalAttributes?: { [key: string]: any };

  get isRequired(): boolean {
    return this.additionalAttributes != null && 'required' in this.additionalAttributes;
  }

  onValueChanged(e:string){
    if(e == null){
      this.selectedValue = null;
    }else{
      this.selectedValue =
    }

  }

  private convertValue(value : string) : any{
    if (value == null || value.trim() == ""){
      return null;
    }

  }

}

