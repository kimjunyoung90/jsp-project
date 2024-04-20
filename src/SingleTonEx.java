public class SingleTonEx {

    //유일한 객체를 하나의 정적 필드에 저장
    private static SingleTonEx instance = new SingleTonEx();

    //유일한 객체에 접근할 수 있는 정적 메서드 정의
    public static SingleTonEx getInstance() {
        return instance;
    }

    // 생성자를 private으로 설정해서 외부에서 객체를 생성하지 못하게 막음
    private SingleTonEx() {}
}
