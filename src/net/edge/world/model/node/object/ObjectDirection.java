package net.edge.world.model.node.object;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The enumerated type whose elements represent the directions for objects.
 * @author lare96 <http://github.com/lare96>
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public enum ObjectDirection {
	/**
	 * The north orientation.
	 */
	NORTH(1, 3, 2),
	
	/**
	 * The south orientation.
	 */
	SOUTH(3, 1, 0),
	
	/**
	 * The east orientation.
	 */
	EAST(2, 0, 3),
	
	/**
	 * The west orientation.
	 */
	WEST(0, 2, 1);
	
	/**
	 * Caches our enum values.
	 */
	private static final ImmutableSet<ObjectDirection> VALUES = Sets.immutableEnumSet(EnumSet.allOf(ObjectDirection.class));
	
	/**
	 * The identification of this direction.
	 */
	private final int id;
	
	/**
	 * The opposite identification of this direction.
	 */
	private final int opposite;
	
	/**
	 * The rotate identification of this direction.
	 */
	private final int rotate;
	
	/**
	 * Creates a new {@link ObjectDirection}.
	 */
	ObjectDirection(int id, int opposite, int rotate) {
		this.id = id;
		this.opposite = opposite;
		this.rotate = rotate;
	}
	
	/**
	 * Gets the opposite object direction for the specified
	 * {@code direction}.
	 * @param direction the direction to get the opposite for.
	 * @return the opposite object direction.
	 */
	public static ObjectDirection getOpposite(ObjectDirection direction) {
		return VALUES.stream().filter(def -> def.opposite == direction.id).findAny().get();
	}
	
	/**
	 * Gets the identification of this direction.
	 * @return the identification of this direction.
	 */
	public final int getId() {
		return id;
	}
	
	/**
	 * A mutable {@link Map} of {@code int} keys to
	 * {@link ObjectDirection} values.
	 */
	private static final Map<Integer, ObjectDirection> values = new HashMap<>();
	
	/**
	 * Populates the {@link #values} cache.
	 */
	static {
		for(ObjectDirection orientation : values()) {
			values.put(orientation.getId(), orientation);
		}
	}
	
	/**
	 * Returns a {@link ObjectDirection} wrapped in an {@link Optional}
	 * for the specified {@code id}.
	 * @param id The game object orientation id.
	 * @return The optional game object orientation.
	 */
	public static Optional<ObjectDirection> valueOf(int id) {
		return Optional.ofNullable(values.get(id));
	}
	
	/**
	 * Gets the rotated direction.
	 * @return rotated direction.
	 */
	public ObjectDirection rotate() {
		return values.get(rotate);
	}
}