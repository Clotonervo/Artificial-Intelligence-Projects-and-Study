import numpy as np
import math

#Utils

def make_move(board,move,player_number):
    """
    This function will execute the move (integer column number) on the given board, 
    where the acting player is given by player_number
    """
    row = 0
    while row < 6 and board[row,move] == 0:
        row += 1
    board[row-1,move] = player_number

def get_valid_moves(board):
    """
    This function will return a list with all the valid moves (column numbers)
    for the input board
    """
    valid_moves = []
    for c in range(7):
        if 0 in board[:,c]:
            valid_moves.append(c)
    return valid_moves

def is_winning_state(board, player_num):
    """
    This function will tell if the player_num player is
    winning in the board that is input
    """
    player_win_str = '{0}{0}{0}{0}'.format(player_num)
    to_str = lambda a: ''.join(a.astype(str))

    def check_horizontal(b):
        for row in b:
            if player_win_str in to_str(row):
                return True
        return False

    def check_verticle(b):
        return check_horizontal(b.T)

    def check_diagonal(b):
        for op in [None, np.fliplr]:
            op_board = op(b) if op else b
            
            root_diag = np.diagonal(op_board, offset=0).astype(np.int)
            if player_win_str in to_str(root_diag):
                return True

            for i in range(1, b.shape[1]-3):
                for offset in [i, -i]:
                    diag = np.diagonal(op_board, offset=offset)
                    diag = to_str(diag.astype(np.int))
                    if player_win_str in diag:
                        return True

        return False

    return (check_horizontal(board) or
            check_verticle(board) or
            check_diagonal(board))

#The players!

class AIPlayer:
    def __init__(self, player_number):
        self.player_number = player_number  #This is the id of the player this AI is in the game
        self.type = 'ai'
        self.player_string = 'Player {}:ai'.format(player_number)
        self.other_player_number = 1 if player_number == 2 else 2  #This is the id of the other player

    def max_value(self, board, alpha, beta, depth, player, opponent):
        valid_moves = get_valid_moves(board)

        #If at the end of the search or end of game, return heuristic
        if is_winning_state(board, player):
            print("---------------- win maxvalue", player)
            return 1000000000
        if is_winning_state(board, opponent):
            print("---------------- win maxvalue", opponent)
            return -1000000000
        elif depth == 0:
            return self.evaluation_function(board)

        v = -math.inf
        for action in valid_moves:
            new_board = np.copy(board)
            make_move(new_board, action, player)
            v = max(v, self.min_value(new_board, alpha, beta, depth - 1, opponent, player))
            if v >= beta:
                return v
            alpha = max(alpha, v)
        return v

    def min_value (self, board, alpha, beta, depth, player, opponent):
        valid_moves = get_valid_moves(board)

        #If at the end of the search or end of game, return heuristic
        if is_winning_state(board, player):
            print("---------------- win minvalue", player)
            return 1000000000
        if is_winning_state(board, opponent):
            print("---------------- win minvalue", opponent)
            return -1000000000
        elif depth == 0:
            return self.evaluation_function(board)

        v = math.inf
        for action in valid_moves:
            new_board = np.copy(board)
            make_move(new_board, action, opponent)
            v = min(v, self.max_value(new_board, alpha, beta, depth - 1, player, opponent))
            if v <= alpha:
                return v
            beta = min(beta, v)
        return v

    def get_alpha_beta_move(self, board):
        """
        Given the current state of the board, return the next move based on
        the alpha-beta pruning algorithm

        This will play against either itself or a human player

        INPUTS:
        board - a numpy array containing the state of the board using the
                following encoding:
                - the board maintains its same two dimensions
                    - row 0 is the top of the board and so is
                      the last row filled
                - spaces that are unoccupied are marked as 0
                - spaces that are occupied by player 1 have a 1 in them
                - spaces that are occupied by player 2 have a 2 in them

        RETURNS:
        The 0 based index of the column that represents the next move
        """
        valid_moves = get_valid_moves(board)

        player = self.player_number
        if player == 1:
            opponent = 2
        elif player == 2:
            opponent = 1

        best_move = 0
        max_value = -math.inf
        depth = 4

        for action in valid_moves:
            new_board = np.copy(board)
            make_move(new_board, action, player)
            print(new_board)
            result = self.min_value(new_board, -math.inf, math.inf, depth, player, opponent)
            print(result)
            if result > max_value:
                max_value = result
                best_move = action

        print(best_move)
        return best_move
        # raise NotImplementedError('Whoops I don\'t know what to do')



    def get_expectimax_move(self, board):
        """
        Given the current state of the board, return the next move based on
        the expectimax algorithm.

        This will play against the random player, who chooses any valid move
        with equal probability

        INPUTS:
        board - a numpy array containing the state of the board using the
                following encoding:
                - the board maintains its same two dimensions
                    - row 0 is the top of the board and so is
                      the last row filled
                - spaces that are unoccupied are marked as 0
                - spaces that are occupied by player 1 have a 1 in them
                - spaces that are occupied by player 2 have a 2 in them

        RETURNS:
        The 0 based index of the column that represents the next move
        """
        raise NotImplementedError('Whoops I don\'t know what to do')

    def evaluate_horizontal(self, board, player, opponent):
        result = 0
        num_adjecent = 0
        opponent_adjecent = 0
        for i in range(0, 6):
            for j in range(0, 4):
                if board[i][j] == player:
                    num_adjecent += 1
                elif board[i][j] != player and num_adjecent != 0:
                    result += math.pow(10, num_adjecent)
                    num_adjecent = 0

                if board[i][j] == opponent:
                    opponent_adjecent += 1
                elif board[i][j] != opponent and opponent_adjecent != 0:
                    result -= math.pow(10, opponent_adjecent)
                    opponent_adjecent = 0

                if j == 6 and opponent_adjecent != 0:
                    result -= math.pow(10, opponent_adjecent)
                elif j == 6 and num_adjecent != 0:
                    result += math.pow(10, num_adjecent)

        return result

    def evaluate_vertical(self, board, player, opponent):
        result = 0
        num_adjecent = 0
        opponent_adjecent = 0
        for i in range(0, 7):
            column = board[:,i]
            for j in range(0, len(column)):
                if column[j] == player:
                    num_adjecent += 1
                elif column[j] != player and num_adjecent != 0:
                    result += math.pow(10, num_adjecent)
                    num_adjecent = 0

                if column[j] == opponent:
                    opponent_adjecent += 1
                elif column[j] != opponent and opponent_adjecent != 0:
                    result -= math.pow(10, opponent_adjecent)
                    opponent_adjecent = 0

                if j == 5 and opponent_adjecent != 0:
                    result -= math.pow(10, opponent_adjecent)
                    opponent_adjecent = 0
                elif j == 5 and num_adjecent != 0:
                    result += math.pow(10, num_adjecent)
                    num_adjecent = 0

        return result

    def evaluate_diagonal(self, board, player, opponent):
        result = 0
        num_adjecent = 0
        opponent_adjecent = 0
        for i in range(0, 6):
            for j in range(0, 7):
                if i + 3 <= 5 and j + 3 <= 6:
                    diagonal_list = [board[i][j], board[i+1][j+1], board[i+2][j+2], board[i+3][j+3]]
                    for k in range(0, len(diagonal_list)):
                        if diagonal_list[k] == player:
                            num_adjecent += 1
                        elif diagonal_list[k] != player and num_adjecent != 0:
                            result += math.pow(2, num_adjecent)
                            num_adjecent = 0

                        if diagonal_list[k] == opponent:
                            opponent_adjecent += 1
                        elif diagonal_list[k] != opponent and opponent_adjecent != 0:
                            result -= math.pow(2, opponent_adjecent)
                            opponent_adjecent = 0

                        if k == 3 and opponent_adjecent != 0:
                            result -= math.pow(2, opponent_adjecent)
                            opponent_adjecent = 0
                        elif k == 3 and num_adjecent != 0:
                            result += math.pow(2, num_adjecent)
                            num_adjecent = 0
                if i - 3 >= 0 and j - 3 >= 0:
                    diagonal_list = [board[i][j], board[i-1][j-1], board[i-2][j-2], board[i-3][j-3]]
                    for k in range(0, len(diagonal_list)):
                        if diagonal_list[k] == player:
                            num_adjecent += 1
                        elif diagonal_list[k] != player and num_adjecent != 0:
                            result += math.pow(2, num_adjecent)
                            num_adjecent = 0

                        if diagonal_list[k] == opponent:
                            opponent_adjecent += 1
                        elif diagonal_list[k] != opponent and opponent_adjecent != 0:
                            result -= math.pow(2, opponent_adjecent)
                            opponent_adjecent = 0

                        if k == 3 and opponent_adjecent != 0:
                            result -= math.pow(2, opponent_adjecent)
                            opponent_adjecent = 0
                        elif k == 3 and num_adjecent != 0:
                            result += math.pow(2, num_adjecent)
                            num_adjecent = 0

        return result

    def evaluation_function(self, board):
        """
        Given the current stat of the board, return the scalar value that 
        represents the evaluation function for the current player
       
        INPUTS:
        board - a numpy array containing the state of the board using the
                following encoding:
                - the board maintains its same two dimensions
                    - row 0 is the top of the board and so is
                      the last row filled
                - spaces that are unoccupied are marked as 0
                - spaces that are occupied by player 1 have a 1 in them
                - spaces that are occupied by player 2 have a 2 in them

        RETURNS:
        The utility value for the current board

        check player horizontal num in row, subtract from other player streak

        check player vertical streak, subtract from player vertical streak

        check player diagonal streak, subtract form other player's vertical streak
        """

        player = self.player_number
        if player == 1:
            opponent = 2
        elif player == 2:
            opponent = 1

        return self.evaluate_horizontal(board, player, opponent) + self.evaluate_vertical(board, player, opponent)


class RandomPlayer:
    def __init__(self, player_number):
        self.player_number = player_number
        self.type = 'random'
        self.player_string = 'Player {}:random'.format(player_number)

    def get_move(self, board):
        """
        Given the current board state select a random column from the available
        valid moves.

        INPUTS:
        board - a numpy array containing the state of the board using the
                following encoding:
                - the board maintains its same two dimensions
                    - row 0 is the top of the board and so is
                      the last row filled
                - spaces that are unoccupied are marked as 0
                - spaces that are occupied by player 1 have a 1 in them
                - spaces that are occupied by player 2 have a 2 in them

        RETURNS:
        The 0 based index of the column that represents the next move
        """
        valid_cols = []
        for col in range(board.shape[1]):
            if 0 in board[:,col]:
                valid_cols.append(col)

        return np.random.choice(valid_cols)


class HumanPlayer:
    def __init__(self, player_number):
        self.player_number = player_number
        self.type = 'human'
        self.player_string = 'Player {}:human'.format(player_number)

    def get_move(self, board):
        """
        Given the current board state returns the human input for next move

        INPUTS:
        board - a numpy array containing the state of the board using the
                following encoding:
                - the board maintains its same two dimensions
                    - row 0 is the top of the board and so is
                      the last row filled
                - spaces that are unoccupied are marked as 0
                - spaces that are occupied by player 1 have a 1 in them
                - spaces that are occupied by player 2 have a 2 in them

        RETURNS:
        The 0 based index of the column that represents the next move
        """

        valid_cols = []
        for i, col in enumerate(board.T):
            if 0 in col:
                valid_cols.append(i)

        move = int(input('Enter your move: '))

        while move not in valid_cols:
            print('Column full, choose from:{}'.format(valid_cols))
            move = int(input('Enter your move: '))

        return move

