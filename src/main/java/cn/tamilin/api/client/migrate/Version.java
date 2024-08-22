package cn.tamilin.api.client.migrate;

public class Version implements Comparable<Version> {

	private int major = -1;

	private int minor = -1;

	private int incremental = -1;

	private String qualifier;

	private int build = -1;

	public Version(String version) {

	}

	public Version(int major, int minor, int incremental, String qualifier, int build) {
		super();
		this.major = major;
		this.minor = minor;
		this.incremental = incremental;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMicro() {
		return incremental;
	}

	public int getIncremental() {
		return incremental;
	}

	public String getQualifier() {
		return qualifier;
	}

	public int getBuild() {
		return build;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(major);
		builder.append('.');
		builder.append(minor);
		if (incremental >= 0) {
			builder.append('.');
			builder.append(incremental);
		}
		if (qualifier != null) {
			builder.append('-');
			builder.append(qualifier);
		}
		if (build > 0) {
			builder.append('-');
			builder.append(build);
		}
		return builder.toString();
	}

	@Override
	public int compareTo(Version o) {
		
		return this.major == o.major ? (this.minor == o.minor ? this.incremental - o.incremental : this.minor - o.minor) : this.major - o.major;
	}
}
