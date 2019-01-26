package com.asimq.artists.bandninja.viewmodels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;

public class SearchResultsViewModel extends AndroidViewModel {

	private LiveData<Artist> mLiveArtist;
	private LiveData<List<Artist>> mLiveArtists;
	private final SearchResultsRepository searchResultsRepository;
	private final BandItemRepository bandItemRepository;

	public SearchResultsViewModel(@NonNull Application application,
			@NonNull SearchResultsRepository searchResultsRepository,
			@NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.searchResultsRepository = searchResultsRepository;
		this.bandItemRepository = bandItemRepository;
	}

	public LiveData<Artist> getArtistInfo(@NonNull String artistName) {
		mLiveArtist = searchResultsRepository.getArtistInfo(artistName);
		return mLiveArtist;
	}

	public LiveData<Boolean> getArtistsRefreshingMutableLiveData() {
		return searchResultsRepository.getArtistsRefreshingMutableLiveData();
	}

	public LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String artistQuery) {
		mLiveArtists = searchResultsRepository.getSearchResultsByArtist(artistQuery);
		return mLiveArtists;
	}

	public LiveData<List<Artist>> getTopArtists() {
		mLiveArtists = searchResultsRepository.getTopArtists();
		return mLiveArtists;
	}

	public LiveData<List<Artist>> getTopArtistsByTag(@NonNull String tag) {
		mLiveArtists = searchResultsRepository.getTopArtistsByTag(tag);
		return mLiveArtists;
	}

	private static Map<String, Boolean> mapOfAttachmentTasks = new HashMap<>();

	public static synchronized void addTask(String taskQueryString){
		mapOfAttachmentTasks.put(taskQueryString, true);
	}

	public static synchronized void removeTask(String taskQueryString){
		mapOfAttachmentTasks.remove(taskQueryString);
	}

	public static synchronized boolean isTasksEmpty(){
		return mapOfAttachmentTasks.isEmpty();
	}

	class ArtistInfoAsyncTask extends AsyncTask<String, Void, Artist> {

		private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
		private final String taskQueryString;

		public ArtistInfoAsyncTask(MediatorLiveData<List<Artist>> artistsLiveDataObservable,
								   String taskQueryString) {
			this.artistsLiveDataObservable = artistsLiveDataObservable;
			this.taskQueryString = taskQueryString;
			addTask(taskQueryString);
		}

		@Override
		protected Artist doInBackground(String... strings) {
			String artistName = strings[0];
			return searchResultsRepository.getArtist(artistName);
		}

		@Override
		protected void onPostExecute(Artist artist) {
			removeTask(taskQueryString);

			if (isTasksEmpty()) {
//				replace artist in map}
			}
		}
	}
}
