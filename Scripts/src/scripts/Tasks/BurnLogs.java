package scripts.Tasks;

import org.powerbot.bot.rt4.client.ItemPile;
import org.powerbot.script.Condition;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Item;
import scripts.Task;
import scripts.Walker;

import java.util.concurrent.Callable;

public class BurnLogs extends Task{

    private final Walker walker = new Walker(ctx);
    final static int TINDERBOX_ID = 590;
    final static int logsToBurn = 0;
    static int logIndex = 0;




    public BurnLogs(ClientContext ctx, int li) {
        super(ctx);
        logIndex = li;
    }
    @Override
    public boolean activate() {
        GroundItem nearestLog = ctx.groundItems.select().id(super.getLogsId()[logIndex]).nearest().poll();
        return ctx.inventory.select().id(super.getLogsId()[logIndex]).count() > 0 &&
                ctx.players.local().animation() == -1 && !ctx.players.local().inMotion() &&
                ctx.players.local().tile().distanceTo(nearestLog) > 0 &&
                ctx.bank.nearest().tile().distanceTo(ctx.players.local()) > 4;
    }

    @Override
    public void execute() {
        System.out.println(super.getInventoriesComplete() + "fires");

        if (ctx.inventory.isFull()) {

            ctx.movement.step(super.getFireSpot());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, 50, 10);
        }
        Item tinderbox = ctx.inventory.select().id(TINDERBOX_ID).poll();
        Item logs = ctx.inventory.select().id(super.getLogsId()[logIndex]).poll();
        tinderbox.interact("Use");
        logs.click();
        super.burnLogs();

        int[] logsArray = super.getLogsId();
        Tile startingLocation = ctx.players.local().tile();


        final int startingLogs = ctx.inventory.select().id(super.getLogsId()).count();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(logsArray[logIndex]).count() != startingLogs &&
                        !ctx.players.local().inMotion() &&
                        ctx.players.local().tile() != startingLocation;
            }
        }, 50, 10);

        if (ctx.inventory.select().id(super.getLogsId()).count() == 0) {
            super.incrementInventories();
        }
    }


}

