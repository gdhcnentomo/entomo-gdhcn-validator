package co.entomo.gdhcn.vo;

public enum ValidationStatus {
	PENDING(-1),SUCCESS(0), FAILED(1);
	private final int value;

    private ValidationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
