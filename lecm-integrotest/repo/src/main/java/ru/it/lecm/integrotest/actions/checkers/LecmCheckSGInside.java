package ru.it.lecm.integrotest.actions.checkers;



import ru.it.lecm.integrotest.FinderBean;
import ru.it.lecm.integrotest.TestFailException;
import ru.it.lecm.integrotest.actions.LecmActionBase;
import ru.it.lecm.integrotest.utils.SGPositionData;

/**
 * Классс для проверки постусловий.
 * @author rabdullin
 */
public class LecmCheckSGInside extends LecmActionBase {

	private final SGPositionData child = new SGPositionData();
	private final SGPositionData parent = new SGPositionData();
	private boolean errorFlag = false;

	public LecmCheckSGInside() {
		super();
		logger.info( this.getClass().getCanonicalName() + " created");
	}

	public SGPositionData getChild() {
		return child;
	}

	public SGPositionData getParent() {
		return parent;
	}

	/**
	 * Вернуть значение флага проверки факта вхождения группы child в parent, 
	 * который соот-ет ошибке. По-умолчанию, false.
	 * @return
	 */
	public boolean isErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(boolean errorFlag) {
		this.errorFlag = errorFlag;
	}

	@Override
	public void run() {
		final FinderBean finder = this.getContext().getFinder();
		final boolean flag = (child != null) && (parent != null)
				&& getContext().getSgNotifier().isSgInside(child.getPos(finder), parent.getPos(finder));
		logger.info( String.format( "check '%s' is inside '%s' -> %s", child, parent, flag));
		if (errorFlag == flag) 
			throw new TestFailException( String.format("%s inside is %s%s", child, (flag ? "" : "NOT "), parent));
	}

}
