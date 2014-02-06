package ru.it.lecm.arm.beans.query;

import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 9:58
 */
public class ArmStaticQuery extends ArmBaseQuery {

    @Override
    public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
        // статический запрос - вернет саму ноду
        List<ArmNode> result = new ArrayList<ArmNode>();
        result.add(node);
        return result;
    }

    @Override
    public ArmBaseQuery getDuplicate() {
        ArmStaticQuery staticQuery = new ArmStaticQuery();
        staticQuery.setSearchQuery(getSearchQuery());
        return staticQuery;
    }
}
