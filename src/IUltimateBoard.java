import java.util.List;

public interface IUltimateBoard extends IBoard{

    Move toLocalMove(Move global);
    Move findOriginOfLocalBoardByMove(Move global);
    Move toGlobalMove(Move local, Move origin);
    IBoard getCurrentLocalBoard();
    IBoard getPreviousBoard();
    IBoard getNextLocalBoard();
    List<IBoard> getAvailableBoard();
    void setCurrentBoard(IBoard board);
    IBoard getLocalBoard(Move global);
}
