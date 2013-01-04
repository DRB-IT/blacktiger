/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.User;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.MeetMeRoom;
import org.asteriskjava.live.MeetMeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Javadoc
 */
public class BlackTigerService implements IBlackTigerService {

    private AsteriskServer asteriskServer;
    private JdbcTemplate jdbcTemplate;
    private UserMapper userMapper = new UserMapper();

    private class UserMapper implements RowMapper {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString("id"), rs.getString("data"));
        }
    }

    public void setAsteriskServer(AsteriskServer asteriskServer) {
        this.asteriskServer = asteriskServer;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public User getUser(String username, String password) {
        return (User) this.jdbcTemplate.queryForObject("select * from sip where id=? and data=? and keyword like 'secret'",
                new Object[]{username, password}, userMapper);
    }

    @Override
    public List<Participant> listParticipants(String roomNo) {
        MeetMeRoom room = asteriskServer.getMeetMeRoom(roomNo);
        List<Participant> result = new ArrayList<Participant>();

        for (MeetMeUser mmu : room.getUsers()) {
            result.add(participantFromMeetMeUser(mmu));
        }
        return result;
    }

    @Override
    public Participant getParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        return mmu == null ? null : participantFromMeetMeUser(mmu);
    }

    @Override
    public void kickParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        if (mmu != null) {
            mmu.kick();
        }
    }

    @Override
    public void muteParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        if (mmu != null) {
            mmu.mute();
        }
    }

    @Override
    public void unmuteParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        if (mmu != null) {
            mmu.unmute();
        }
    }

    private MeetMeUser getMeetMeUser(String roomNo, String participantId) {
        MeetMeRoom room = asteriskServer.getMeetMeRoom(roomNo);
        Integer id = Integer.parseInt(participantId);
        for (MeetMeUser mmu : room.getUsers()) {
            if (mmu.getUserNumber() == id) {
                return mmu;
            }
        }
        return null;
    }

    private Participant participantFromMeetMeUser(MeetMeUser user) {
        return new Participant(user.getUserNumber().toString(),
                user.getChannel().getCallerId().getNumber(),
                user.isMuted());
    }
}
