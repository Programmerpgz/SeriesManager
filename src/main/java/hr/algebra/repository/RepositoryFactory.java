package hr.algebra.repository;

import hr.algebra.databaserepository.*;

public final class RepositoryFactory {
    private RepositoryFactory(){}
    public static ISeriesRepository series(){
        return DBSeriesRepository.getInstance();
    }
    public static IActorRepository actors(){
        return DBActorRepository.getInstance();
    }
    public static IDirectorRepository directors(){
        return DBDirectorRepository.getInstance();
    }
    public static IUserRepository users(){
        return DBUserRepository.getInstance();
    }
    public static IWatchlistRepository watchlist() {
        return DBWatchlistRepository.getInstance();
    }

}
