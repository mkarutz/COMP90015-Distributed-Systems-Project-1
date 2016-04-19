public class LockRequest {
	private String userName;
	private String secret;
	private Connection connection;

	@Override
	public int hashCode() {
		return userName.hashCode() * 37 + secret.hashCode() * 41;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceOf LockRequest)) {
			return false;
		}
		LockRequest other = (LockRequest) obj;
		return userName.equals(other.userName) && secret.equals(other.secret);
	}
}
