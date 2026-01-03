import { Component } from '@angular/core';
import { DataStoreService } from '../../../services/data-store.service';
import { OnInit } from '@angular/core';
import { TagService } from '../../../services/tag.service';

@Component({
    selector: 'post-search',
    templateUrl: './post-search.component.html',
    styleUrls: []
})
export class PostSearchComponent implements OnInit{
        
    sortByOptions = [
        {id: 1, name: 'Search Term'}, 
        {id: 2, name: 'Trending'},
        {id: 3, name: 'Highest Voted'}, 
        {id: 4, name: 'New'}
    ];
    sortBy = this.sortByOptions[0];

    searchTerm: string = '';
    

    searchTimeframe = [
        'Hour', 
        'Day', 
        'Week', 
        'Month', 
        'Year'
    ];

    searchTagOptions = ['All'];
    searchTag = 'All';

    constructor(
        private dataStoreService: DataStoreService,
        private tagService: TagService
    ) {}

    // LifeCycle hooks
    ngOnInit(): void {
        this.tagService.getTags().subscribe({
            next: response => {
                this.searchTagOptions = response.response;
                console.log(this.searchTagOptions);
            },
            error: error => {
                console.error('Error Loading Tags', error);
            }
        });
    }

    searchPosts(searchTerm: string, searchTag: string) {
        console.log("-->", searchTag);

        this.dataStoreService.searchPosts(searchTerm, searchTag);
        this.searchTerm = '';
    }

    isSubmitButtonDisabled(): boolean {
        return false; // Not disabled
    }
}



