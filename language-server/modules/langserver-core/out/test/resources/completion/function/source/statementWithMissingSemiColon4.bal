type testObject object {
    int field1 = 12;
    int field2 = 12;

    public function __init(int field1, int field2) {
        self.field1 = field1;
        self.field2 = field2;
    }
    
    function testObjectFunction(int a) {
	}
};

function getInt(string param) returns int {
    return 12;
}

function testFunction() {
    string testString = "";
    int intVal = getInt(testString.fromJsonString().mergeJson())
}
