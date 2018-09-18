package toDoDatebase;

public class ToDoDbSchema {
    public static final class ToDoTable{
        public static final String TABLE_NAME = "toDoList";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String THING = "thing";
        }
    }
}
