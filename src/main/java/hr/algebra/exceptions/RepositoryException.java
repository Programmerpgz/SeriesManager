package hr.algebra.exceptions;

public class RepositoryException extends RuntimeException {
    @SuppressWarnings("unused")
    private final String repositoryName;
    public RepositoryException(String repositoryName, String message) {
        super(repositoryName + ": " + message);
        this.repositoryName = repositoryName;
    }
    public RepositoryException(String repositoryName, String message, Throwable cause) {
        super(repositoryName + ": " + message, cause);
        this.repositoryName = repositoryName;
    }
    public static RepositoryException nullEntity(String repositoryName) {
        return new RepositoryException(repositoryName, "entity cannot be null!");
    }
    public static RepositoryException operationNotSuccessful(String repositoryName, String operation, Throwable cause) {
        return new RepositoryException(repositoryName, "operation '" + operation + "' failed.", cause);
    }

    @SuppressWarnings("unused")
    public String getRepositoryName() {
        return repositoryName;
    }
}
