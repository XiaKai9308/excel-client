package cn.tamilin.api.client;

/**
 * capacity 0-3,31
 * @author Summer
 */
public enum State implements ValueEnum {

	/**
	 * 启用状态
	 */
	ENABLED(1 << 0),

	/**
	 * 锁定状态
	 */
	LOCKED(1 << 1),

	/**
	 * 隐藏状态
	 */
	HIDDEN(1 << 2),

	/**
	 * 过期状态
	 */
	EXPIRED(1 << 3),

	/**
	 * 逻辑删除状态
	 */
	DELETED(1 << 31);

	private int id;

	private State(int id) {
		this.id = id;
	}

	@Override
	public int value() {
		return this.id;
	}

	public static State ofValue(int value) {
		for (State role : values())
			if (role.value() == value)
				return role;
		return null;
	}
}
