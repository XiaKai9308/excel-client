package cn.tamilin.api.client;

public interface ValueEnum {

	public int value();

	public default String code() {
		return null;
	};

	public String name();

	public default boolean in(int flag) {
		return (this.value() & flag) == this.value();
	}
}
