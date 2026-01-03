import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";


@Injectable({
    providedIn: 'root',
})
export class ModalService {
    private modalSubject = new BehaviorSubject<string | null>(null);

    modal$ = this.modalSubject.asObservable();

    setModal(modal: string | null) {
        console.log("Modal Service Hit!");
        this.modalSubject.next(modal);
    }
}