import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-house-form',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './house-form.html',
  styleUrl: './house-form.scss',
})
export class HouseForm implements OnInit{
  private fb = inject(FormBuilder);

  @Input() initialData?: HouseResponse;
  @Output() saveForm = new EventEmitter<any>();

  houseForm = this.fb.group({
    // Textos principales
    title: ['', [Validators.required, Validators.minLength(10)]],
    description: ['', [Validators.required, Validators.maxLength(1000)]],
    price: [0, [Validators.required, Validators.min(0)]],

    // Selects / Enums
    houseType: ['', Validators.required], // Se enlazará con tu Enum HouseType

    // Dimensiones y estancias
    size: [0, [Validators.required, Validators.min(15)]],
    rooms: [0, [Validators.required, Validators.min(0)]],
    baths: [0, [Validators.required, Validators.min(0)]],

    // Características (Booleanos, arrancan en false igual que en tu base de datos)
    lift: [false],
    furnished: [false],
    airConditioned: [false],
    terrace: [false],
    balcony: [false],
    garage: [false],
    storage: [false],
    pool: [false],
    commonAreas: [false],
    petsAllowed: [false],

    address: this.fb.group({
      street: ['', Validators.required],
      number: [null as number | null, [ Validators.min(1)]],
      floor: [''],
      door: [''],
      city: ['', Validators.required],
      cp: [null as number | null, [Validators.required]],
      province: ['', Validators.required],
      country: ['España', Validators.required],
    })
  });

  ngOnInit() {
    if (this.initialData) {
      this.houseForm.patchValue(this.initialData);
    }
  }

  onSubmit() {
    if (this.houseForm.valid) {
      this.saveForm.emit(this.houseForm.getRawValue());
    }
  }



}
