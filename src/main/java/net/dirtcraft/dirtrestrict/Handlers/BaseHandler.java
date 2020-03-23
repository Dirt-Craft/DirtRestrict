package net.dirtcraft.dirtrestrict.Handlers;

import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;

public abstract class BaseHandler {
    protected final DirtRestrict dirtRestrict = DirtRestrict.getInstance();
    protected final RestrictionList restricts = dirtRestrict.getRestrictions();
}
