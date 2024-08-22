package cn.tamilin.api.client;

public enum Label implements ValueEnum {

	ID(1),

	CODE(2),

	NAME(3),

	BRAND(4),

	TYPE(5),

	OE(6),

	MODEL(7),

	QUANTITY(8),

	PRICE(9);

	private int value;

	private Label(int value) {
		this.value = value;
	}

	@Override
	public String code() {
		return this.name().toLowerCase();
	}

	@Override
	public int value() {
		return this.value;
	}
}
