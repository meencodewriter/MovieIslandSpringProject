package kr.or.ticketing.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.admin.model.vo.Schedule;
import kr.or.admin.model.vo.Theater;
import kr.or.giftticket.model.vo.GiftTicket;
import kr.or.ticketing.model.dao.TicketingDao;
import kr.or.ticketing.model.vo.Pay;
import kr.or.ticketing.model.vo.TheaterLocalCount;
import kr.or.ticketing.model.vo.Ticketing;
import kr.or.ticketing.model.vo.TicketingInfo;
import kr.or.ticketing.model.vo.TicketingSchedule;

@Service
public class TicketingService {
	@Autowired
	private TicketingDao dao;

	public ArrayList<Schedule> selectSchedule(String dataDay) {
		ArrayList<Schedule> list = dao.selectSchedule(dataDay);

		return list;
	}

	public Theater selectOntTheaterBrch(int theaterNo) {
		Theater theater = dao.selectOneTheaterBrch(theaterNo);
		return theater;
	}

	public ArrayList<TheaterLocalCount> choiceDayTheater(String movieTitle, String choiceDataDay) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("movieTitle", movieTitle);
		map.put("choiceDataDay", choiceDataDay);

		ArrayList<TheaterLocalCount> list = dao.choiceDayTheater(map);

		return list;
	}

	public ArrayList<String> choiceLocalBranch(String movieTitle, String choiceDataDay,
			String theaterLocal) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("movieTitle", movieTitle);
		map.put("choiceDataDay", choiceDataDay);
		map.put("theaterLocal", theaterLocal);

		ArrayList<String> list = dao.choiceLocalBranch(map);

		return list;
	}

	public ArrayList<TicketingSchedule> choiceBranchSchedule(String movieTitle, String choiceDataDay, String theaterBranch) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("movieTitle", movieTitle);
		map.put("choiceDataDay", choiceDataDay);
		map.put("theaterBranch", theaterBranch);

		ArrayList<TicketingSchedule> list = dao.choiceBranchSchedule(map);

		return list;
	}

	public ArrayList<GiftTicket> selectGiftTicket(int memberNo) {
		ArrayList<GiftTicket> giftList = dao.selectGiftTicket(memberNo);
		return (ArrayList<GiftTicket>) giftList;
	}

	
	

	public int insertPay(Pay pay, TicketingInfo ticketingInfo) {
		int result = dao.insertPay(pay, ticketingInfo);
		int payNo = dao.selectLatestPay();
		System.out.println(payNo);
		ticketingInfo.getChoiceDtDay();	//상영시간(날짜형식->+시간형식)
		ticketingInfo.getScheduleNo();	//스케쥴넘버
		/*
		ticketingInfo.getNumOfPeople();	//연령(성인2청소년1)
		ticketingInfo.getJoinSeats();	//좌석(
		 */
		Ticketing ticketing = new Ticketing();
		ticketing.setTicketingTime(ticketingInfo.getChoiceDtDay());
		ticketing.setPayNo(payNo);
		ticketing.setScheduleNo(ticketingInfo.getScheduleNo());
		String[] seatArr = ticketingInfo.getJoinSeats().split("/");
		String[] peopleArr = ticketingInfo.getCountArr().split("/");
		
		for(int i=0;i<seatArr.length;i++) {
			ticketing.setSeat(seatArr[i]);
			String age = peopleArr[i];
			ticketing.setTicketingAge(Integer.parseInt(age));
			
			result += dao.insertTicketing(ticketing);
		}
		
		
		/*
		if(result>0) {
			
		}
		*/
		return result;
		
	}

	public ArrayList<Schedule> selectSeat(int scheduleNo) {
		ArrayList<Schedule> list = dao.selectSeat(scheduleNo);
		return list;
	}

	public String getMovieFile(String movieTitle) {
		
		return dao.getMovieFile(movieTitle);
	}

	

	
}
