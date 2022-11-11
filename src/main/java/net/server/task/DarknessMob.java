package net.server.task;

import config.YamlConfig;
import net.server.Server;
import net.server.world.World;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DarknessMob implements Runnable {
    class DarknessTimeRange {
        public int startHour, endHour;
        public int startMin, endMin;

        public DarknessTimeRange(int startHour, int startMin, int endHour, int endMin) {
            this.startHour = startHour;
            this.startMin = startMin;
            this.endHour = endHour;
            this.endMin = endMin;
        }
    };

    private List<DarknessTimeRange> timeRanges = new ArrayList<>();
    private boolean darknessSwitch;

    public DarknessMob() {
        darknessSwitch = false;
        timeRanges.add(new DarknessTimeRange(5,0,6,30));
        timeRanges.add(new DarknessTimeRange(13,0,14,30));
        timeRanges.add(new DarknessTimeRange(21,0,22,30));
    }

    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        boolean matchRange = false;
        long curTime = calendar.getTimeInMillis();
        for (DarknessTimeRange range : timeRanges) {
            calendar.set(Calendar.HOUR_OF_DAY, range.startHour);
            calendar.set(Calendar.MINUTE, range.startMin);
            long startTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, range.endHour);
            calendar.set(Calendar.MINUTE, range.endMin);
            long endTime = calendar.getTimeInMillis();
            if (curTime >= startTime && curTime <= endTime) {
                matchRange = true;
                break;
            }
        }
        if (matchRange && !darknessSwitch) {
            for (World w : Server.getInstance().getWorlds()) {
                w.setServerMessage("黑魔法师的一丝力量渗透到枫之大陆，怪物变得更强更多，请冒险家小心");
                w.setMobhprate(2);
                w.setMobrate(3);
                w.dropMessage(6, "欢乐时光就要开始了~");
            }
            darknessSwitch = true;
        } else if (!matchRange && darknessSwitch) {
            for (World w : Server.getInstance().getWorlds()) {
                w.setServerMessage(YamlConfig.config.worlds.get(0).server_message);
                w.setMobhprate(1);
                w.setMobrate(1);
                w.dropMessage(6, "黑魔法师的力量削弱了，枫之大陆回归了平静");
            }
            darknessSwitch = false;
        }
    }
}
