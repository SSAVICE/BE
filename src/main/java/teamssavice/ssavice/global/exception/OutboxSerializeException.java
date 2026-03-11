package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class OutboxSerializeException extends CustomException {

    private static final String DEFAULT_TITLE = "Outbox Serialize Error";

    public OutboxSerializeException() {
        super(ErrorCode.OUTBOX_PAYLOAD_SERIALIZE_FAILED, DEFAULT_TITLE);
    }
}
