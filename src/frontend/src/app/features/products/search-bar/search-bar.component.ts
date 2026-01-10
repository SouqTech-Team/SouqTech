import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss']
})
export class SearchBarComponent implements OnInit, OnDestroy {
  @Output() newQueryEvent = new EventEmitter<string>();
  /* Form */
  public searchField: FormControl<string> = new FormControl('', { nonNullable: true });;
  public searchForm: FormGroup;
  /* Events */
  private destroy$ = new Subject<void>();

  constructor(private formBuilder: FormBuilder) {
    this.searchForm = formBuilder.group({ search: this.searchField })
  }
  
  ngOnInit(): void {
    this.searchField.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
        )
      .subscribe((q) => {
        this.newQueryEvent.next(q ?? '');
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  onClear(): void {
    this.searchField.setValue('');
    // Avoid debounceTime
    this.newQueryEvent.next('');
  }

}
